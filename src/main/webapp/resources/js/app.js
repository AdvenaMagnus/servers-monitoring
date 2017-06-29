
var monitoringModule = angular.module('main',['ui.bootstrap', 'ngAnimate']);

monitoringModule.controller('mainController', function($scope, $http, serversFactory, $uibModal, $timeout) {

	/**Fetch current date from web-server*/
	if(typeof $scope.currentDate == 'undefined'){
		$http.get('/currentdate').then(function(response){
			$scope.currentDate = response.data.date;
		}, function(){
			console.log("Error recieving currentDate")
		});
	}

	/**Fetch servers list*/
	if(serversFactory.servers == null){
		//$http.get('/servers/light').then(function(response){
		$http.get('/servers').then(function(response){
			serversFactory.servers = response.data;
			$scope.servers = serversFactory.servers;
		}, function(){
			console.log("Error recieving servers")
		});
	} else $scope.servers = serversFactory.servers;

	$scope.openPopup = function (server) {
		if(typeof server != 'undefined') $http.get("/servers/detailinfo/" + server.id);
		var modalInstance = $uibModal.open({
			templateUrl: '/html/server-detail.html',
			controller: 'serverCtrl',
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			//appendTo: parentElem,
			resolve: {
				server: server,
				serversScope: $scope
			}
		});
		modalInstance.result.then(function (server) {
			//console.log('popup ok');
			//console.log(server);
		}, function () {
			console.log('popup canceled')
		});
	};

	var serversManupaulationFuncs = {};
	CRUDconf($scope, $http, serversFactory);
	statusesConf($scope, serversFactory, $http);
	SSEconf($scope, serversFactory, serversManupaulationFuncs);
	detailInfoconf($scope, $http, serversManupaulationFuncs);
	//timerConf($scope, $timeout);
});

/**Logic for server detail info */
function detailInfoconf($scope, $http, serversManupaulationFuncs){
    $scope.showInfo = function(server){
        if(typeof server.isInfoAvail == 'undefined'){
            server.isInfoAvail = true;
            //$('#info-area-'+server.id).hide();
        } else server.isInfoAvail = !server.isInfoAvail;

        $('#info-tr-'+server.id).slideToggle();
        $('#info-area-'+server.id).slideToggle();

        if(server.isInfoAvail) {
            $http.get("/servers/detailinfo/" + server.id).then(function (response) {
                console.log("recieving server detailinfo");
                serversManupaulationFuncs.updateDetailInfo(response.data);
            }, function () {
                console.log("Error recieving server detailinfo")
            });
        }
    };

    $scope.detailInfoOpenImg = function (server) {
       if(typeof server.isInfoAvail=='undefined'){
            return '/images/show_details_down.png';
		}
		if(server.isInfoAvail) return '/images/show_details_up.png'
		else return '/images/show_details_down.png'
    }

}

/**DAO for statuses*/
function statusesConf($scope, serversFactory, $http){

	/**Update server status on local server object*/
    $scope.getServerStatus = function(server){
        //if(serversFactory.isUpdateStatus) {
        $http.get("/servers/status/" + server.id).then(function(response){
            //console.log("recieving server status")
        }, function(){
            console.log("Error recieving server status")
        });
    };

	/**Update server ping on local server object*/
	$scope.getServerPing = function (server) {
        $http.get("/servers/ping/" + server.id).then(function(response){
            server.ping = response.data.ping;
        }, function(){
            console.log("Error recieving servers")
        });
	};

	/**Update statuses on all servers */
	$scope.updateAllNow = function(){
		console.log("update from "+ serversFactory.isUpdateStatus);
		$scope.clearStatuses(serversFactory.servers);
		serversFactory.servers.forEach(function(s){
			$scope.getServerStatus(s);
			$scope.getServerPing(s);
		});
		console.log("to "+ serversFactory.isUpdateStatus);
	};

	/**Update certain server*/
	$scope.updateServer = function(server){
		$scope.clearServerData(server);
		$scope.getServerStatus(server);
		$scope.getServerPing(server);
	};

	/**Days after last update real server*/
	$scope.howOldRevision = function(server){
		if(typeof server.revisionDate != 'undefined')
			return serversFactory.daysBetweenDates(new Date(serversFactory.currentDate()), new Date(server.revisionDate))
	};

	$scope.colorStatus = function(server) {
		if(server.serverStatusCached != null) {
            if (server.serverStatusCached.status == 'online') return 'success';
            if (server.serverStatusCached.status == 'offline') return 'danger';
            if (server.serverStatusCached.ping == 'нет') return 'warning';
        }
		return ''
	};

	$scope.getDaysTextColor = function(days){
		if(days<=30) return 'green';
		if(days>30 && days<=90) return 'orange';
		if(days>90) return 'red';
	};

    /** Clear all status data on servers*/
    $scope.clearStatuses = function(array){
        array.forEach(function(s){
            $scope.clearServerData(s)
        });
    };

    /**Clear status data on server*/
    $scope.clearServerData = function(s){
        s.ping = null;
        //s.status = null;
        if(typeof s.serverStatusCached != 'undefined'){
            s.serverStatusCached.lastStatus = s.serverStatusCached.status;
            s.serverStatusCached.status = null;
		}
        //s.detailInfo = null;
    };

    $scope.formatIntForTime = function (num) {
		return num<10? '0'+num : num;
    }
}


/**Configurations for statuses auto update*/
// function timerConf($scope, $timeout){
//     $scope.runTimeOut = function () {
//         console.log('timer update');
//         $scope.updateAllNow();
//         if ($scope.isAutoUpdate)
//             currentTimer = $timeout($scope.runTimeOut, $scope.timeOutUpdateS * 1000);
//     };
//
// 	$scope.isAutoUpdate = true; //= false;
// 	$scope.timeOutUpdateS = 120;
// 	var currentTimer;
//     currentTimer = $timeout($scope.runTimeOut, $scope.timeOutUpdateS*1000);
// 	// $scope.goAutoUpdate = function(){
// 	// 	if($scope.isAutoUpdate) {
// 	// 		currentTimer = $timeout($scope.runTimeOut, $scope.timeOutUpdateS*1000);
// 	// 	} else $timeout.cancel(currentTimer);
// 	// };
//
// }

/**SSE configuration*/
function SSEconf($scope, serversFactory, serversManupaulationFuncs){
    var sourceDelete = new EventSource('/sse/servers');
    sourceDelete.onmessage = function (event) {
        console.log(event);
        var dataParsed = serversFactory.parseJson(event.data);
        $scope.$apply(function () {
            switch (dataParsed.type) {
                case 'delete':
                    serversManupaulationFuncs.deleteServer(dataParsed.msg);
                    break;
                case 'update':
                    serversManupaulationFuncs.updateServer(dataParsed.msg);
                    break;
                case 'status':
                    serversManupaulationFuncs.statusUpdate(dataParsed.msg);
                    break;
                case 'detailinfo':
                    serversManupaulationFuncs.updateDetailInfo(dataParsed.msg);
                    break;
            }
        })
    };
    sourceDelete.onerror = function (event) {
        console.log('sse delete error');
    };

    serversManupaulationFuncs.deleteServer = function (data) {
        //$scope.$apply(function(){
		var serverToDelete = $scope.servers[$scope.findServerIndexById($scope.servers, data)];
		if(typeof serverToDelete != 'undefined') {
            new Notification('Сервер ' + serverToDelete.name + ' удален', {
                tag: "delete-server",
                body: serverToDelete.ip,
                icon: "/images/favicon_servers.ico"
            });
        }
        serversFactory.servers = $.grep(serversFactory.servers, function (value) {
            return value.id != data;
        });
        $scope.servers = serversFactory.servers;
        //})
    };

    serversManupaulationFuncs.updateServer = function(data){
        //$scope.$apply(function(){
            var parsedServer = data;
            //console.log(parsedServer);
        	$scope.updateServerLocal(serversFactory.servers, parsedServer);
            $scope.servers = serversFactory.servers;
        //})
	};

    serversManupaulationFuncs.statusUpdate = function (data) {
        //$scope.$apply(function(){
            //var parsedStatus = serversFactory.parseJson(data);
            var parsedStatus = data;
            var serverLocal = $scope.servers[$scope.findServerIndexById($scope.servers, parsedStatus.server_id)];
            if(typeof serverLocal != 'undefined'){
            	if(typeof serverLocal.serverStatusCached!= 'undefined' && serverLocal.serverStatusCached.lastStatus!=null && serverLocal.serverStatusCached.lastStatus != parsedStatus.server_status.status){
                    new Notification(serverLocal.name, {
                        tag : "new-status-"+serverLocal.name,
                        body : "Статус: " + parsedStatus.server_status.status,
                        icon: "/images/favicon_servers.ico"
                    });
				}
                serverLocal.serverStatusCached = parsedStatus.server_status;
                if(typeof serverLocal.serverStatusCached.revisionDate != 'undefined' && serverLocal.serverStatusCached.revisionDate != null)
                    serverLocal.lastUpdateDays = serversFactory.daysBetweenDates(
                        new Date(serversFactory.formatDate($scope.currentDate)),
                        new Date(serversFactory.formatDate(serverLocal.serverStatusCached.revisionDate)));
            }
        //})
    };

    serversManupaulationFuncs.updateDetailInfo = function (data) {
        //$scope.$apply(function(){
            //var parsedStatus = serversFactory.parseJson(data);
            var parsedStatus = data;
            var serverLocal = $scope.servers[$scope.findServerIndexById($scope.servers, parsedStatus.server_id)];
            if(typeof serverLocal != 'undefined'){
                serverLocal.detailInfo = parsedStatus.detailInfo;
            }
        //})
    }

}

function CRUDconf($scope, $http, serversFactory){
	/** Find index of server by it's id */
	$scope.findServerIndexById = function(array, id){
		var result = -1;
		array.forEach(function(s){
			if(s.id==id) result = array.indexOf(s);
		});
		return result;
	};

	/** Update server in array, if server does not exist - add to array; updating by server.id*/
	$scope.updateServerLocal = function(array, server){
		var index = $scope.findServerIndexById(array, server.id);
		//if(index!=-1)array[index] = server;
		if(index!=-1){
			var serverFound = array[index];
            serverFound.name = server.name;
            serverFound.ip = server.ip;
            serverFound.notices = server.notices;
            if(server.detailInfo!=null){
                serverFound.detailInfo.systemLogin = server.detailInfo.systemLogin;
                serverFound.detailInfo.systemPassword = server.detailInfo.systemPassword;
                serverFound.detailInfo.serverLogin = server.detailInfo.serverLogin;
                serverFound.detailInfo.serverPassword = server.detailInfo.serverPassword;
			}
        }
		else {
			array.push(server);
		}
	};

	/**Create server object on Web-server and on local */
	$scope.createServer = function(server, callback){
		$http.put('/servers/create', server).then(function(response){
			//console.log("Fetched created server");
			//console.log(response.data);
			//servers.push(response.data);
			callback(response.data);
		}, function(){
			console.log("Error recieving created server")
		});
	};

	/** Update exist server object on web-server and on local */
	$scope.updateServerOnServer = function(server, callback){
		$http.post('/servers/update', server).then(function(response){
			//console.log("Fetched updated server");
			//console.log(response.data);
			//servers.push(response.data);
			callback(response.data);
		}, function(){
			console.log("Error recieving updated server")
		});
	};

	/**Delete server*/
	$scope.deleteServer = function (server, index) {
		if (server != null && server.id != null) {
			$scope.deleteServerFromDB(server)
		}
	};

	/** Delete server from web-server */
	$scope.deleteServerFromDB =  function(server, callback){
		$http.delete('/servers/delete/'+server.id).then(function(response){
			console.log("Deleted");
			console.log(server);
			//servers.push(response.data);
			//callback();
		}, function(){
			console.log("Error deleting server")
		});
	};
}

