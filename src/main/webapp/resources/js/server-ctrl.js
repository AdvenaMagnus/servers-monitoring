/**
 * Created by Grey on 23.03.2017.
 */

monitoringModule.controller('serverCtrl', function($scope, $uibModalInstance, server) {

    $scope.server = server;
    $scope.ipReg = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    $scope.checkIp = function(ip){
        //if(ip!=null)return ip.match($scope.ipReg);
        //else return false;
        return true;
    };

    var closePopup = function () {
        $uibModalInstance.close($scope.server)
    };

    $scope.ok = function () {
        if($scope.server.id != null) {
            crudServiceForServers.updateServer($scope.server, closePopup);
        }

        if(typeof $scope.server.id == 'undefined'){
            new Notification('Новый сервер', {
                tag : "new-server",
                body : $scope.server.name,
                icon: "/images/favicon_servers.ico"
            });
            crudServiceForServers.createServer($scope.server, closePopup);
        }
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };


});