function errorAlert(title) {
    swal({
        //position: 'top-end',
        type: 'error',
        title: title,
        showConfirmButton: false,
        timer: 1000
    });
}

function successAlert(title) {
    swal({
        //position: 'top-end',
        type: 'success',
        title: title,
        showConfirmButton: false,
        timer: 1000
    });
}

function errorAlertContainText(title, text) {
    swal({
        type: 'error',
        title: title,
        text: text
    });
}

function animatedAlert(title) {
    swal({
        title: title,
        animation: false,
        customClass: 'animated tada'
    });
}

function timerAlert(title, target) {
    let timerInterval;
    swal({
        type: 'success',
        title: title,
        timer: 1000,
        allowOutsideClick: false,
        onOpen: () => {
            swal.showLoading();
            timerInterval = setInterval(() => {}, 100)
        },
        onClose: () => {
            clearInterval(timerInterval)
        }
    }).then((result) => {
        if (result.dismiss === swal.DismissReason.timer) {
            location.href = target;
        }
    })
}

function timerErrorAlert(title, target) {
    let timerInterval;
    swal({
        type: 'error',
        title: title,
        timer: 1000,
        allowOutsideClick: false,
        onOpen: () => {
            swal.showLoading();
            timerInterval = setInterval(() => {}, 100)
        },
        onClose: () => {
            clearInterval(timerInterval)
        }
    }).then((result) => {
        if (result.dismiss === swal.DismissReason.timer) {
            location.href = target;
        }
    })
}