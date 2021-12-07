$(function() {
  $("#user-table").dataTable({
    // DataTablesを日本語化する
    language: {
      url: "/webjars/datatables-plugins/i18n/Japanese.json"
    },
    // 各種ボタンを有効化する
    dom: "frtip",
  });
  
  $("#user-table-print").dataTable({
	    // DataTablesを日本語化する
	    language: {
	      url: "/webjars/datatables-plugins/i18n/Japanese.json"
	    },
	    // 各種ボタンを有効化する
	    dom: "Bfrtip",
	    buttons: [
          
            {
                extend: 'print',
                text: '<i class ="bi bi-printer me-2"></i >印刷',
                titleAttr: 'PDF',
                autoPrint: true,
                exportOptions: {
                    columns: ':visible:not(:eq(-1))'
                },
                customize: function (win) {
                    $(win.document.body).find('h1').css('text-align','center');
                }
            }
        ]
	  });
});

