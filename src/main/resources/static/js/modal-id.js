let modal = null

const focusableSelector = "button, input, a, textarea"
let focusables = []

let previouslyFocusedElement = null


document.querySelectorAll(".js-modal").forEach(a => {
    a.addEventListener("click", openModal)
    
})


function openModal(e){
    e.preventDefault();

    modal = document.querySelector(e.target.getAttribute("href"))

    focusables = Array.from(modal.querySelectorAll(focusableSelector))
    previouslyFocusedElement = document.querySelector(":focus")
    modal.style.display = null
    focusables[0].focus()
    modal.removeAttribute("aria-hidden")
    modal.setAttribute("aria-modal", "true")
    modal.addEventListener("click", closeModal)

    modal.querySelector(".js-close-modal").addEventListener("click", closeModal)
    modal.querySelector(".js-modal-stop").addEventListener("click", stopPropagation)


    const userId = e.target.getAttribute("data-user-id");
    console.log(e.target.classList[e.target.classList.length-1])
    console.log('User ID:', userId);

    if(e.target.classList[e.target.classList.length-1]=="delete-button"){
        let deleteHref = `/admin/deleteAccount?userId=${userId}`
        modal.querySelector(".confirm-btn").setAttribute("href", deleteHref)
    }else if(e.target.classList[e.target.classList.length-1]=="desac-button"){
        let deleteHref = `/admin/desacAccount?userId=${userId}`
        modal.querySelector(".confirm-btn").setAttribute("href", deleteHref)
    }

    
}

function closeModal(e){
    if(modal==null) return
    if(previouslyFocusedElement!= null) previouslyFocusedElement.focus()
    e.preventDefault()

    modal.style.display = "none"
    modal.setAttribute("aria-hidden", "true")
    modal.removeAttribute("aria-modal")
    modal.removeEventListener("click", closeModal)
    modal.querySelector(".js-close-modal").removeEventListener("click", closeModal)
    modal.querySelector(".js-modal-stop").removeEventListener("click", stopPropagation)
    
    modal.querySelector(".confirm-btn").setAttribute("href", "/")

    modal = null


    stopPropagation(e)


}


function focusInModal(e){
    e.preventDefault()
    focusables.findIndex(f => f===modal.querySelector(":focus"))

    if(e.shiftKey == true){
        index--
    }
    index++

    if(index>=focusables.length){
        index = 0
    }
    if(index < 0){
        index = focusables.length-1
    }
    focusables[index].focus()
}


const stopPropagation = function (e){
    e.stopPropagation()
}


window.addEventListener("keydown", function (e){
    if(e.key === "Escape" || e.key === "Esc"){
        closeModal(e)
    }
    if(e.key ==="Tab" && modal != null){
        focusInModal(e)
    }
})