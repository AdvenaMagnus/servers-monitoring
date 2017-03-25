<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Мониторинг серверов</title>
	<link rel="shortcut icon" href="/images/favicon_servers.ico" type="image/x-icon" />
	<script src="https://code.jquery.com/jquery-3.2.0.min.js"></script>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.6/angular.min.js"></script>
	<script src="/js/lib/ui-bootstrap-tpls-2.5.0.min.js"></script>
	<script src="/js/app.js"></script>
	<script src="/js/services.js"></script>
	<script src="/js/server-ctrl.js"></script>
</head>
<body ng-app="main">
<h1 style="padding-left: 20px;text-align: center">Мониторинг серверов</h1>
<div ng-controller="mainController">
	<%--<a href ng-click="updateAllNow()">Обновить статусы у всех серверов</a>--%>
	<table class="table table-striped" style="width:auto;" align="center">
		<tr>
			<th style="width:200px;"> Наименование</th>
			<th style="width:150px;"> IP-адрес</th>
			<th style="width:100px;"> Пинг</th>
			<th style="width:100px;"> Система</th>
			<th style="width:100px;"> Ревизия</th>
			<th style="width:150px;"> Дата ревизии</th>
			<%--<th style="width:150px;"> Дней после обновления</th>--%>
			<th style="width:150px;"><a href ng-click="updateAllNow()">Обновить все</a></th>
			<th style="width:100px;"></th>
			<th style="width:30px;"></th>
		</tr>
		<tr ng-repeat-start="server in servers | orderBy:'name'" class="{{colorStatus(server)}}">
			<td><a href ng-click="openPopup(server)">{{server.name}}</a>
				<%--<a href  ng-click="showInfo(server)">Примечание</a>--%>
			</td>
			<td> {{server.ip}}</td>
			<td>{{server.ping}}</td>
			<td>{{server.status}}</td>
			<td>{{server.revision}}</td>
			<td>{{server.revisionDate}}
				{{server.lastUpdateDays!=null? '(' : ''}}<font color="{{getDaysTextColor(server.lastUpdateDays)}}">{{server.lastUpdateDays}}</font>{{server.lastUpdateDays!=null? ')' : ''}}
			</td>
			<td><a href  ng-click="updateServer(server)">Обновить статус</a></td>
			<td><a href="http://{{server.ip}}" target="_blank">Перейти</a></td>
			<td><a href ng-click="deleteServer(server, $index)">X</a></td>
			<%--<td></td>--%>
		</tr>
		<tr ng-repeat-end ng-show="false" class="ng-hide"> <td colspan="8"> test</td> </tr>
		<tr  class="table table-striped">
			<td colspan="8">
				<span style="padding-right:10px;">Автообновление статусов</span>
				<input type="checkbox" ng-model="isAutoUpdate" name="Включить автообновление" ng-click="goAutoUpdate()"/>
				<span style="padding-left:10px;" ng-show="isAutoUpdate==true">интервал, сек.</span>
				<input style="width:40px" type="text" ng-model="timeOutUpdateS" ng-show="isAutoUpdate==true" />
			</td>
		</tr>
	</table>
	<div style="text-align: center"><a href ng-click="openPopup()">Добавить сервер</a></div>
</div>

</body>
</html>