function forbin(){
	window.alert("抱歉客官，留言功能暂未开放，敬请期待。");
}
var winHeight = 0;
if (window.innerHeight){
winHeight = window.innerHeight;
}
else if ((document.body) && (document.body.clientHeight)){
winHeight = document.body.clientHeight;
}
var intro = document.getElementById("intro");
intro.style.height = winHeight+"px";
var full = document.getElementById("fullpage");
full.style.height = winHeight+"px";