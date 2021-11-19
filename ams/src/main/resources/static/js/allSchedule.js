document.addEventListener('DOMContentLoaded', function() {
	  var calendarEl = document.getElementById('allSchedule');
        var calendar = new FullCalendar.Calendar(calendarEl, {
			locale:'ja',
			dayCellContent: function(e) {
    		e.dayNumberText = e.dayNumberText.replace('日', '');
			},
			eventSources:[
				{
					url:'/allScheduleURL',
					display:'none',
					id: 0
				},{
					url:'/number',
					id: 1
				}
			],
			dateClick: function(info) {
				var date2 = info.dateStr;
								
								var ev = info.view.calendar.getEvents();
								var st= '';
								for(var i = 0, len = ev.length; i < len; i++){
									if(ev[i].startStr == date2){
										st += ev[i].title; 
										st += '\n';
									}
								}
								var con = confirm(st);
								con.then(function(dialogResult) { // resolve値にボタンが押された結果を渡す
								  if(dialogResult === customConfirm.OK) { // 例えばこんな定数っぽい値と比較
								    // OKの処理
								  } else {
								    // キャンセルとか
								  }
								})
			  },
			  eventClick: function(info){
				  var ev = info.view.calendar.getEvent();
				  confirm(ev);
					var st= '';
					for(var i = 0, len = ev.length; i < len; i++){
						if(ev[i].startStr == date2){
							st += ev[i].title; 
							st += '\n';
						}
					}
					confirm(st);
				  
			  }
			
	});
        calendar.render();
      });