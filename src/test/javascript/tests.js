/**
 * Created by Alexander on 30.06.2017.
 */
// describe("pow", function() {
//
//     it("возводит в n-ю степень", function() {
//         assert.equal(pow(2, 3), 8);
//     });
//
// });

describe("Servers manipulation tests", function() {
    describe("Add and get server", function() {

        var server = {
            id:1
        };

        var tempServers = servers;

        it("Add server", function() {
            serversDAO.addServer(server);
            assert.isTrue(servers.length>0);
            assert.equal(tempServers, servers);
        });

        it("Get server", function() {
            var fetchedServer = serversDAO.getServerById(server.id);
            assert.equal(fetchedServer, server);
            assert.equal(tempServers, servers);
        });

        after(function() {
            servers = [];
            assert.isTrue(servers.length==0);
        });

    });

    describe("Remove server", function() {
        var server1 = {
            id:1,
            meta: 'server1'
        };
        var server2 = {
            id:2,
            meta: 'server2'
        };

        var tempServs = servers;

        before(function() {
            assert.isTrue(servers.length==0);
            serversDAO.addServer(server1);
            serversDAO.addServer(server2);
            assert.isTrue(servers.length==2);
            //assert.equal(tempServs, servers);
        });

        it("Getting index by id test", function() {
            var i = serversDAO.indexById(server2.id);
            assert.isTrue(i==1);
        });

        it("Remove server", function() {
            serversDAO.removeServerById(server1.id);
            assert.isTrue(servers.length==1);
            assert.equal(servers[0], server2);
            //assert.equal(tempServs, servers);
        });

        after(function() {
            servers = [];
            assert.isTrue(servers.length==0);
        });

    });

    describe("Update server", function() {
        var server1 = {
            id:1,
            meta: 'server1'
        };
        var server2 = {
            id:1,
            meta: 'server2'
        };
        var server3 = {
            id:2,
            meta: 'server3'
        };

        var tempServs = servers;

        before(function() {
            assert.isTrue(servers.length==0);
            serversDAO.addServer(server1);
            assert.isTrue(servers.length==1);
            //assert.equal(tempServs, servers);
        });

        it("Replace server by id", function() {
            serversDAO.updateServer(server2);
            assert.isTrue(servers[0]==server2);
            assert.isTrue(servers[0].meta == server2.meta);
            assert.isTrue(servers.length==1);
        });

        it("If server to update doesn't exist - add one", function() {
            serversDAO.updateServer(server3);
            assert.isTrue(servers[1]==server3);
            assert.isTrue(servers[1].meta == server3.meta);
            assert.isTrue(servers.length==2);
        });

        after(function() {
            servers = [];
            assert.isTrue(servers.length==0);
        });

    });

    describe("Update detailinfo", function() {

        var server1 = {
            id: 1,
            meta: 'server1'
        };

        var server2 = {
            id: 2,
            meta: 'server2'
        };

        var detailinfo1 = {
            info: 'detailinfo1'
        };

        var detailinfo2 = {
            info: 'detailinfo1'
        };

        before(function() {
            assert.isTrue(servers.length==0);
            serversDAO.addServer(server1);
            serversDAO.addServer(server2);
            assert.isTrue(servers.length==2);
            //assert.equal(tempServs, servers);
        });

        it("Add detailInfo object to server by id", function() {
            serversDAO.updateDetailInfo(1, detailinfo1);
            assert.isTrue(servers[0]==server1);
            assert.isTrue(servers[0].detailInfo == detailinfo1);
            assert.isTrue(servers.length==2);
        });

        it("If server with id doesn't exist - do nothing", function() {
            serversDAO.updateDetailInfo(5, detailinfo2);
            assert.isTrue(servers[0]==server1);
            assert.isTrue(servers[1]==server2);
            assert.isTrue(typeof servers[1].detailInfo == 'undefined');
            assert.isTrue(servers.length==2);
        });

        after(function() {
            servers = [];
            assert.isTrue(servers.length==0);
        });

    });

});

describe("SSE service", function() {

    describe("Deleting server", function() {

        var server = {
            id:1
        };
        var id = 1;

        before(function() {
            assert.isTrue(servers.length==0);
            serversDAO.addServer(server);
            assert.isTrue(servers.length==1);
            //assert.equal(tempServs, servers);
        });


        it("Delete server", function() {
            sseService.deleteServer(id);
            assert.isTrue(servers.length==0);
        });

        after(function() {
            servers = [];
            assert.isTrue(servers.length==0);
        });

    });

    describe("Updating server status", function() {

        currentDate = '21.12.2012';

        var server = {
            id:1
        };

        var msg = {
            server_id:1,
            server_status: {
                id:2,
                status: 'Online',
                revision: 0001,
                revisionDate: '23.12.2012'
            }
        };

        before(function() {
            assert.isTrue(servers.length==0);
            serversDAO.addServer(server);
            assert.isTrue(servers.length==1);
            //assert.equal(tempServs, servers);
        });


        it("Updating server status", function() {
            sseService.statusUpdate(msg);
            assert.isTrue(servers.length==1);
            assert.isTrue(servers[0].serverStatusCached.status == msg.server_status.status);
            assert.isTrue(servers[0].serverStatusCached.revision == msg.server_status.revision);
            assert.isTrue(servers[0].serverStatusCached.revisionDate == msg.server_status.revisionDate);
        });

        after(function() {
            servers = [];
            assert.isTrue(servers.length==0);
        });

    });

    describe("Updating server ping", function() {
        var server = {
            id:1
        };

        var server2 = {
            id:2,
            ping:'5 мс'
        };

        var msg = {
            server_id:1,
            ping: '8 мс'
        };

        var msg2 = {
            server_id:2,
            ping: '9 мс'
        };

        before(function() {
            assert.isTrue(servers.length==0);
            serversDAO.addServer(server);
            serversDAO.addServer(server2);
            assert.isTrue(servers.length==2);
            //assert.equal(tempServs, servers);
        });

        it("Updating ping when server has no ping data", function() {
            sseService.updatePing(msg);
            assert.isTrue(servers.length==2);
            assert.isTrue(servers[0].ping == msg.ping);
        });

        it("Updating ping when server already has ping data", function() {
            sseService.updatePing(msg2);
            assert.isTrue(servers.length==2);
            assert.isTrue(servers[1].ping == msg2.ping);
        });

    });


});

