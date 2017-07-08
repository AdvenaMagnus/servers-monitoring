/**
 * Created by Alexander on 30.06.2017.
 */


var currentDate = {};
function getCurrentDate() {
    $.get('/currentdate', function(response){
        currentDate = response.date;
    });
}

/** Servers */
var servers = [];
var serversDAO = {
  refresh : function () {
      console.log("Standard refresh, nothing'd happen")
  },

  getServerById: function (id) {
      var result;
      servers.forEach(function(server){
          if(server.id==id) {
              result = server;
          }
      });
      return result;
  },

  addServer: function(server){
      servers.push(server);
  },

  removeServerById: function (id) {
      servers.splice(serversDAO.indexById(id), 1);
  },

  indexById: function (id) {
      var result;
      servers.forEach(function(server){
          if(server.id==id) {
               result = servers.indexOf(server);
          }
      });
      return result;
  },

  updateServer: function (server) {
    //var oldServer = serversDAO.getServerById(server.id)
    var index = serversDAO.indexById(server.id);
    if(typeof index == 'undefined'){
        serversDAO.addServer(server);
    } else {
        servers[index] = server;
    }
  },

  updateDetailInfo: function (id, detailinfo) {
      var server = serversDAO.getServerById(id);
      if(typeof server != 'undefined') {
          server.detailInfo = detailinfo;
      }
  }
};

/** */
var crudServiceForServers = {
    loadServers: function (callback) {
        $.get( "/servers", function(response) {
            response.forEach(function (newServer) {
                serversDAO.addServer(newServer);
            });
            //servers = response;
            // stickTo.servers = servers;
            // stickTo.$apply();
            //callback();
            serversDAO.refresh();
        });
    },

    createServer: function (server, callback) {
        $.ajax({
            url: '/servers/create',
            type: 'PUT',
            data: JSON.stringify(server),
            contentType: 'application/json',
            success: function (data) {
                if(typeof callback!='undefined'){
                    callback();
                }
            }
        });
    },

    updateServer: function (server, callback) {
        // $.post( '/servers/update', JSON.stringify(server))
        //     .done(function () {
        //         callback();
        //     })
        //     .fail(function() {
        //     console.log("Error when update server")
        // });
        $.ajax({
            url: '/servers/update',
            type: 'POST',
            data: JSON.stringify(server),
            contentType: 'application/json',
            success: function (data) {
                if(typeof callback!='undefined'){
                    callback();
                }
            }
        });
    },

    deleteServer: function (server) {
        $.ajax({
            url: '/servers/delete/'+server.id,
            type: 'DELETE',
            error: function (response) {
                console.log("Error creating server")
            }
        });
    },

    fetchDetailInfo: function (server) {
        $.get('/servers/detailinfo/' + server.id, function (response) {
            serversDAO.updateDetailInfo(response.server_id, response.detailInfo)
            serversDAO.refresh()
        });
    },

    updateStatus: function (server) {
        $.get("/servers/status/" + server.id, function (response) {
        }).fail(function () {
            console.log("Error fetching status")
        });
    },

    updatePing: function (server) {
        $.get("/servers/ping/" + server.id, function (response) {
            server.ping = response.ping
            serversDAO.refresh();
        }).fail(function () {
            console.log("Error receiving ping")
        });
    }
};

/**SSE configuration*/
function SSEconf(){
    var source = new EventSource('/sse/servers');
    source.onmessage = function (event) {
        //console.log(event);
        var dataParsed = mutils.parseJson(event.data);
        switch (dataParsed.type) {
            case 'delete':
                sseService.deleteServer(dataParsed.msg);
                break;
            case 'update':
                sseService.updateServer(dataParsed.msg);
                break;
            case 'status':
                sseService.statusUpdate(dataParsed.msg);
                break;
            case 'ping':
                sseService.statusUpdate(dataParsed.msg);
                break;
            case 'detailinfo':
                sseService.updateDetailInfo(dataParsed.msg);
                break;
        }
    };
    source.onerror = function (event) {
        console.log('sse delete error');
    };
}

var sseService = {};

sseService.deleteServer = function (data) {
    var serverToDelete = serversDAO.getServerById(data);
    serversDAO.removeServerById(data);
    if(typeof serverToDelete != 'undefined') {
        new Notification('Сервер ' + serverToDelete.name + ' удален', {
            tag: "delete-server",
            body: serverToDelete.ip,
            icon: "/images/favicon_servers.ico"
        });
    }
    serversDAO.refresh();
};

sseService.updateServer = function(data){
    var parsedServer = data;
    serversDAO.updateServer(parsedServer);
    //serversDAO.refresh();
};

sseService.statusUpdate = function (data) {
    var parsedStatus = data;
    var serverLocal = serversDAO.getServerById(parsedStatus.server_id);
    if (typeof serverLocal != 'undefined') {
        if (typeof serverLocal.serverStatusCached != 'undefined') {
            if (serverLocal.serverStatusCached.status != null)
                serverLocal.serverStatusCached.lastStatus = serverLocal.serverStatusCached.status;
            if (serverLocal.serverStatusCached.lastStatus != null && serverLocal.serverStatusCached.lastStatus != parsedStatus.server_status.status) {
                new Notification(serverLocal.name, {
                    tag: "new-status-" + serverLocal.name,
                    body: "Статус: " + parsedStatus.server_status.status,
                    icon: "/images/favicon_servers.ico"
                });
            }
        }
    }
    serverLocal.serverStatusCached = parsedStatus.server_status;
    if (typeof serverLocal.serverStatusCached.revisionDate != 'undefined' && serverLocal.serverStatusCached.revisionDate != null)
        serverLocal.lastUpdateDays = mutils.daysBetweenDates(
            new Date(mutils.formatDate(currentDate)),
            new Date(mutils.formatDate(serverLocal.serverStatusCached.revisionDate)));
    serversDAO.refresh();
};

sseService.updateDetailInfo = function (data) {
    //$scope.$apply(function(){
    //var parsedStatus = serversFactory.parseJson(data);

    // var parsedStatus = data;
    // var serverLocal = $scope.servers[$scope.findServerIndexById($scope.servers, parsedStatus.server_id)];
    // if(typeof serverLocal != 'undefined'){
    //     serverLocal.detailInfo = parsedStatus.detailInfo;
    // }

    //})
};

sseService.updatePing = function (data) {
    var server = serversDAO.getServerById(data.server_id)
    server.ping = data.ping
};



//getCurrentDate();
//SSEconf();