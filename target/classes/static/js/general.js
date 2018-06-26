function scrollFunction() {
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
        document.getElementById("topBtn").style.display = "block";
    } else {
        document.getElementById("topBtn").style.display = "none";
    }
}

function topFunction() {
    $('html, body').animate({scrollTop : 0},500);
	return false;
}

function goToParticipante(id) {
	location.href = '/pollafacil/participante/?id=' + id;
}

$(document).ready(function() {
	window.onscroll = function() {scrollFunction()};
	$('a[href^="#"]').click(function() {
		$('html,body').animate({ scrollTop: $(this.hash).offset().top}, 500);
		return false;
		e.preventDefault();
	});
});