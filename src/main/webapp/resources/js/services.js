monitoringModule.factory("serversFactory", function ($http) {
    var servers;

    return {
        servers: servers,
        isUpdateStatus: false,
        daysBetweenDates: function (date1, date2) {
            var timeDiff = Math.abs(date2.getTime() - date1.getTime());
            return Math.ceil(timeDiff / (1000 * 3600 * 24));
        },
        formatDate: function (dateString) {
            var dateSplitted = dateString.split(".");
            return dateSplitted[1] + '/' + dateSplitted[0] + '/' + dateSplitted[2];
        },
        parseJson: function (json) {
            console.log('parse ' + json);
            //return angular.fromJson(json);
            return JSON.parse(json);
        }
    }
});