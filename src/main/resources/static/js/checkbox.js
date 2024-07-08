let $salesButton = document.getElementById("sales");
let $purchasesButton = document.getElementById("purchases")
let $open = document.getElementById("open");
let $current = document.getElementById("current");
let $won = document.getElementById("won");
let $currentVente = document.getElementById("currentVente");
let $notstarted = document.getElementById("notstarted");
let $finished = document.getElementById("finished");

$salesButton.addEventListener("click", function() {
	$open.disabled = true;
	$open.checked = false;
	$current.disabled = true;
	$current.checked = false;
	$won.disabled = true;
	$won.checked = false;
	$currentVente.disabled = false;
	$notstarted.disabled = false;
	$finished.disabled = false;
})

$purchasesButton.addEventListener("click", function() {
	$open.disabled = false;
	$current.disabled = false;
	$won.disabled = false;
	$currentVente.disabled = true;
	$currentVente.checked = false;
	$notstarted.disabled = true;
	$notstarted.checked = false;
	$finished.disabled = true;
	$finished.checked = false;
})

document.addEventListener("DOMContentLoaded", function() {
	if ($salesButton.checked == true) {
		$currentVente.disabled = false;
		$notstarted.disabled = false;
		$finished.disabled = false;
	}
	if ($purchasesButton.checked == true) {
		$open.disabled = false;
		$current.disabled = false;
		$won.disabled = false;
	}
});
