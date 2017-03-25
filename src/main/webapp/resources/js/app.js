
var monitoringModule = angular.module('main',['ui.bootstrap']);

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
		$http.get('/servers').then(function(response){
			serversFactory.servers = response.data;
			$scope.servers = serversFactory.servers;
		}, function(){
			console.log("Error recieving servers")
		});
	} else $scope.servers = serversFactory.servers;

	$scope.openPopup = function (server) {
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

	CRUDconf($scope, $http, serversFactory);
	statusesConf($scope, serversFactory);
	SSEconf($scope, serversFactory);
	timerConf($scope, $timeout);

	$scope.showInfo = function(server){
		if(typeof server.isInfoAvail == 'undefined'){
			server.isInfoAvail = true;
		} else server.isInfoAvail = !server.isInfoAvail;
	};

});


/**DAO for statuses*/
function statusesConf($scope, serversFactory){

	/**Update server status on local server object*/
	$scope.getServerStatus = function(server){
		//if(serversFactory.isUpdateStatus) {
		$.getJSON("/servers/status/" + server.id, function (data) {
			console.log("Fetched server status");
			console.log(data);
			$scope.$apply(function () {
				server.status = data.status;
				server.revision = data.revision;
				server.revisionDate = data.revisionDate;
				if(typeof server.revisionDate != 'undefined' && server.revisionDate != null)
					server.lastUpdateDays = serversFactory.daysBetweenDates(
							new Date(serversFactory.formatDate($scope.currentDate)),
							new Date(serversFactory.formatDate(server.revisionDate)));
			});
		});
		//}
	};

	/**Update server ping on local server object*/
	$scope.getServerPing = function (server) {
		$.getJSON("/servers/ping/" + server.id, function (data) {
			$scope.$apply(function () {
				server.ping = data.ping;
			});
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
		if (server.status == 'online') return 'success';
		if (server.status == 'offline') return 'danger';
		if (server.ping == 'нет') return 'warning';
		return ''
	};

	$scope.getDaysTextColor = function(days){
		if(days<=7) return 'green';
		if(days>7 && days<=30) return 'orange';
		if(days>30) return 'red';
	};
}


/**Configurations for statuses auto update*/
function timerConf($scope, $timeout){
	$scope.isAutoUpdate = false;
	$scope.timeOutUpdateS = 10;
	var currentTimer;
	$scope.goAutoUpdate = function(){
		if($scope.isAutoUpdate) {
			currentTimer = $timeout($scope.runTimeOut, $scope.timeOutUpdateS*1000);
		} else $timeout.cancel(currentTimer);
	};

	$scope.runTimeOut = function () {
		console.log('timer update');
		$scope.updateAllNow();
		if ($scope.isAutoUpdate)
			currentTimer = $timeout($scope.runTimeOut, $scope.timeOutUpdateS * 1000);
	};
}

/**SSE configuration*/
function SSEconf($scope, serversFactory){
	var sourceDelete = new EventSource('/sseDelete');
	sourceDelete.onmessage = function (event) {
		console.log(event);
		$scope.$apply(function(){
			serversFactory.servers = $.grep(serversFactory.servers, function (value) {
				return value.id != event.data;
			});
			$scope.servers = serversFactory.servers;
		})
	};
	sourceDelete.onerror = function (event) {
		console.log('sse delete error');
	};

	var sourceUpdate = new EventSource('/sseUpdate');
	sourceUpdate.onmessage = function (event) {
		//console.log(event);
		$scope.$apply(function(){
			var parsedServer = serversFactory.parseJson(event.data)
			console.log(parsedServer);
			$scope.updateServerLocal(serversFactory.servers, parsedServer);
			$scope.servers = serversFactory.servers;
		})
	};
	sourceUpdate.onerror = function (event) {
		console.log('sse update error');
	};
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

	/** Update server in array, if server does not exist - add to array*/
	$scope.updateServerLocal = function(array, server){
		var index = $scope.findServerIndexById(array, server.id);
		if(index!=-1)array[index] = server;
		else {
			array.push(server);
		}
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
		s.status = null;
		s.revision = null;
		s.revisionDate = null;
		s.lastUpdateDays = null;
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
			var collback = function () {
				if (index > -1) {
					serversFactory.servers = $.grep(serversFactory.servers, function (value) {
						return value.id != server.id;
					});
					$scope.servers = serversFactory.servers;
				}
				else {
					console.log(server);
					console.log('has index ' + index);
				}
			};
			$scope.deleteServerFromDB(server, collback)
		}
	};

	/** Delete server from web-server */
	$scope.deleteServerFromDB =  function(server, callback){
		$http.delete('/servers/delete/'+server.id).then(function(response){
			console.log("Deleted");
			console.log(server);
			//servers.push(response.data);
			callback();
		}, function(){
			console.log("Error deleting server")
		});
	};
}

