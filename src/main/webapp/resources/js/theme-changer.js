/**
 * Created by Alexander on 30.06.2017.
 */
var themes = {
    "default": "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css",
    "superhero" : "https://bootswatch.com/superhero/bootstrap.min.css",
    "slate" : "https://bootswatch.com/slate/bootstrap.min.css",
    "geo" : "/css/lib/bootstrap.geo.min.css"
}

$(function(){
    var themesheet = $('<link href="'+themes['geo']+'" rel="stylesheet" />');
    themesheet.appendTo('head');
    $('.theme-link').click(function(){
        var themeurl = themes[$(this).attr('data-theme')];
        themesheet.attr('href',themeurl);
    });
});