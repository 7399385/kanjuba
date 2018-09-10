 jQuery(document).ready(function($) {
 
    $(".scroll").click(function(event){   
    event.preventDefault();
    $('html,body').animate({scrollTop:$(this.hash).offset().top}, 400,'swing');
    });


    function handleResize() {
    var h = jQuery(window).height();
    jQuery('.fullpage').css({'height': h + 'px'});
    }
    jQuery(function() {
    handleResize();
    jQuery(window).resize(function() {
        handleResize();
    });
    });


  






var wow = new WOW(
  {
    boxClass:     'wowload',      // animated element css class (default is wow)
    animateClass: 'animated', // animation css class (default is animated)
    offset:       0,          // distance to the element when triggering the animation (default is 0)
    mobile:       true,       // trigger animations on mobile devices (default is true)
    live:         true        // act on asynchronously loaded content (default is true)
  }
);
wow.init();




$('.carousel').swipe( {
     swipeLeft: function() {
         $(this).carousel('next');
     },
     swipeRight: function() {
         $(this).carousel('prev');
     },
     allowPageScroll: 'vertical'
 });


  });
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


