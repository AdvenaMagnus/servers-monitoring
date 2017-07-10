
var monitoringModule = angular.module('main',['ui.bootstrap', 'ngAnimate']);

monitoringModule.controller('mainController', function($scope, $uibModal, $timeout) {

    getCurrentDate();
    SSEconf();

	serversDAO.refresh = function () {
        $scope.$apply();
    };
    $scope.servers = servers;
    crudServiceForServers.loadServers();

	$scope.openPopup = function (server) {
		if(typeof server != 'undefined') {
            crudServiceForServers.fetchDetailInfo(server);
        }
		var modalInstance = $uibModal.open({
			templateUrl: '/html/server-detail.html',
			controller: 'serverCtrl',
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			//appendTo: parentElem,
			resolve: {
				server: server
			}
		});
		modalInstance.result.then(function (server) {
			//console.log('popup ok');
			//console.log(server);
		}, function () {
			console.log('popup canceled')
		});
	};

	CRUDconf($scope);
	statusesConf($scope);
	detailInfoconf($scope);
});

/**Logic for server detail info */
function detailInfoconf($scope, $http, serversManupaulationFuncs){
    $scope.showInfo = function(server){
        if(typeof server.isInfoAvail == 'undefined'){
            server.isInfoAvail = true;
            //$('#info-area-'+server.id).hide();
        } else server.isInfoAvail = !server.isInfoAvail;

        if(server.isInfoAvail) {
            crudServiceForServers.fetchDetailInfo(server);
        }

        $('#info-tr-'+server.id).slideToggle();
        $('#info-area-'+server.id).slideToggle();

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
function statusesConf($scope, $http){

	/**Update statuses on all servers */
	$scope.updateAllNow = function(){
		$scope.clearStatuses($scope.servers);
        $scope.servers.forEach(function(s){
            crudServiceForServers.updateStatus(s);
            crudServiceForServers.updatePing(s);
		});
	};

	/**Update certain server*/
	$scope.updateServer = function(server){
		$scope.clearServerData(server);
        crudServiceForServers.updateStatus(server);
        crudServiceForServers.updatePing(server);
	};

	/**Days after last update real server*/
	$scope.howOldRevision = function(server){
		if(typeof server.revisionDate != 'undefined')
			return mutils.daysBetweenDates(new Date(currentDate), new Date(server.revisionDate))
	};

	$scope.colorStatus = function(server) {
		if(!server.inService) {
            if (server.serverStatusCached != null) {
                if (server.serverStatusCached.status == 'online') return 'success';
                if (server.serverStatusCached.status == 'offline') return 'danger';
                if (server.serverStatusCached.ping == 'нет') return 'warning';
            }
            return ''
        } else {
			return 'info';
		}
	};

	$scope.getDaysTextColor = function(days){
		if(days<=30) return '#008000';
		if(days>30 && days<=90) return '#D68B00';
		if(days>90) return '#C71300';
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
}

function CRUDconf($scope, $http){
	/**Delete server*/
	$scope.deleteServer = function (server, index) {
		if (server != null && server.id != null) {
            crudServiceForServers.deleteServer(server)
		}
	};
}