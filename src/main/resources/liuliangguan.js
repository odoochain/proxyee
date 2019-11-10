var tasks=["1115073","1115074","1118466","1115076","1116894","1115077","1118090","1118091","1118823","1115078","1115075"];
setTimeout(function () {
    for(var i=0;i<tasks.length;i++){
        doTask(tasks[i]);
    }
},1000);

function doTask(id) {
    $.post("https://professorhe.sh.chinamobile.com/datau/datau/xrPneumaShangBao.du?eventId=1114647&channelId=hn&loginSign="+loginSign,{taskId:id},function (data,status) {

    });
}