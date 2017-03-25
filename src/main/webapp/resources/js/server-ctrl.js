/**
 * Created by Grey on 23.03.2017.
 */

monitoringModule.controller('serverCtrl', function($scope, $uibModalInstance, server, serversFactory, serversScope) {

    $scope.server = server;
    $scope.ipReg = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    $scope.checkIp = function(ip){
        //if(ip!=null)return ip.match($scope.ipReg);
        //else return false;
        return true;
    };

    $scope.ok = function () {
        if($scope.server.id != null) {
            var callback = function (server) {
                //serversFactory.updateServer(serversScope.servers, server);
                serversScope.updateServerLocal(serversFactory.servers, server);
                $uibModalInstance.close($scope.server);
            };
            serversScope.updateServerOnServer($scope.server, callback);
        }

        if(typeof $scope.server.id == 'undefined'){
            var callback = function(server){
                //serversScope.servers.push(server);
                serversScope.updateServerLocal(serversFactory.servers, server);
                $uibModalInstance.close($scope.server);
            };
            serversScope.createServer($scope.server, callback);
        }

    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };


});