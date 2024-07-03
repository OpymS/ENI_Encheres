window.onscroll = function() {
    shrinkNavbar();
};

function shrinkNavbar() {
    var navbar = document.getElementById("navbar");
    var logoImg = document.getElementById("logo-img");
    if (document.body.scrollTop > 50 || document.documentElement.scrollTop > 50) {
        navbar.classList.add("navbar-shrink");
    } else {
        navbar.classList.remove("navbar-shrink");
    }
}
