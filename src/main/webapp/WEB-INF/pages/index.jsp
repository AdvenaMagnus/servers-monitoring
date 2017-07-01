<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Мониторинг серверов</title>
	<link rel="stylesheet" type="text/css" href="/css/main.css">
	<link rel="shortcut icon" href="/images/favicon_servers.ico" type="image/x-icon" />
	<script src="https://code.jquery.com/jquery-3.2.0.min.js"></script>
	<script src="/js/theme-changer.js"></script>
	<script src="/js/utils.js"></script>
	<script src="/js/servers-model.js"></script>

	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.6/angular.min.js"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.6/angular-animate.js"></script>
	<script src="/js/lib/ui-bootstrap-tpls-2.5.0.min.js"></script>
	<script src="/js/app.js"></script>
	<script src="/js/server-ctrl.js"></script>
</head>
<body ng-app="main">
<div style="text-align: right">
	<a href="#" data-theme="default" class="theme-link">Default | </a>
	<a href="#" data-theme="geo" class="theme-link">GEO | </a>
	<a href="#" data-theme="slate" class="theme-link">Dark</a>
</div>

<h1 style="padding-left: 20px;text-align: center">Мониторинг серверов</h1>
<div ng-controller="mainController">
	<div style="text-align: center; padding-bottom:10px;">
		<a href ng-click="updateAllNow()" style="text-align: center">Обновить все |</a>
		<a href="/statistics" style="text-align: center"> Статистика</a>
	</div>
	<table class="table table-striped" style="width:auto;" align="center">
		<tr>
			<th style="width:200px;"> Наименование</th>
			<th style="width:150px;"> IP-адрес</th>
			<th style="width:100px;"> Пинг</th>
			<th style="width:100px;"> Система</th>
			<th style="width:100px;"> Ревизия</th>
			<th style="width:150px;">Дата ревизии</th>
			<th style="width:200px;">Примечания</th>
			<th style="width:100px;"></th>
			<th style="width:30px;"></th>
		</tr>
		<tr ng-repeat-start="server in servers | orderBy:'name'" class="{{colorStatus(server)}}">
			<td class="serverspadding" ng-init="updateServer(server)">
				<a href ng-click="openPopup(server)">{{server.name}}</a>
				<a href ng-click="showInfo(server)" >
					<img border="0" ng-src="{{detailInfoOpenImg(server)}}"></a>
			</td>
			<td class="serverspadding">{{server.ip}}</td>
			<td class="serverspadding">{{server.ping}}</td>
			<td class="serverspadding">{{server.serverStatusCached.status}}</td>
			<td class="serverspadding">{{server.serverStatusCached.revision}}</td>
			<td class="serverspadding">
				{{server.serverStatusCached.revisionDate}}
				{{server.lastUpdateDays!=null? '(' : ''}}<font color="{{getDaysTextColor(server.lastUpdateDays)}}">{{server.lastUpdateDays}}</font>{{server.lastUpdateDays!=null? ')' : ''}}
				</br>
				<span style="font-size:9px;">{{server.serverStatusCached.date}}</span>
			</td>
			<td class="serverspadding" style="white-space: pre-wrap;">{{server.notices}}</td>
			<td class="serverspadding"><a href="http://{{server.ip}}" target="_blank">Перейти</a></td>
			<td class="serverspadding"><a href ng-click="deleteServer(server, $index)">X</a></td>
		</tr>
		<%--<tr ng-repeat-end ng-show="server.isInfoAvail" class="info-area ng-hide"> <td colspan="9"> <div class="info-area ng-hide" ng-show="server.isInfoAvail">test1 </br> test 2 </br> test 3</div></td> </tr>--%>
		<tr ng-repeat-end id="info-tr-{{server.id}}" style="display:none;" >
			<td colspan="9">
				<div style="display:none;" id="info-area-{{server.id}}">
					<table width="100%">
						<tr>
							<td>
								<span style="color:darkGrey"> Логин в системе:</span> {{server.detailInfo.systemLogin}}
								</br> <span style="color:darkGrey">Пароль в системе:</span> {{server.detailInfo.systemPassword}}
							</td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>
	<div style="text-align: center"><a href ng-click="openPopup()">Добавить сервер</a></div>
</div>

</body>
</html>