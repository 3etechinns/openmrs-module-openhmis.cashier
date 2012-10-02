curl(
    { baseUrl: openhmis.config.wwwUrlRoot + 'js/' },
    [
        'lib/jquery',
        'model/paymentMode',
        'view/generic',
        'view/paymentMode'
        
    ],
    function($, openhmis) {
        $(function() {
            openhmis.startAddEditScreen(openhmis.PaymentMode, {
                addEditViewType: openhmis.PaymentModeAddEditView,
                listFields: ['name', 'description']
            });
        });
    }
);