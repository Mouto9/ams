document.addEventListener('DOMContentLoaded', function() {
	  var calendarEl = document.getElementById('adminAllSchedule');
        var calendar = new FullCalendar.Calendar(calendarEl, {
        	locale:'ja',
        	themeSystem: 'bootstrap',
        	contentHeight: 'auto',
        	showNonCurrentDates: false,
        	dayMaxEventRows: true,
			view: {
				dayGridMonth: {
					dayMaxEventRows: 1
				}
			},
			dayCellContent: function(e) {
    		e.dayNumberText = e.dayNumberText.replace('日', '');
			},
			eventMouseEnter: function(mouseEnterInfo){
				mouseEnterInfo.event = eventSources;
				mouseEnterInfo.element = title;
				
			},
			eventSources:[
				{
					url:'/allScheduleURL',
					display:'none',
				},{
					url:'/number',
				},{
					url:'/notAttend',
				},{
					url:'/holiday',
					display:'background'
				}
			],

/*			dateClick: function(info) {
				var date2 = info.dateStr;
								
				var ev = info.view.calendar.getEvents();
				var st= '';
				for(var i = 0, len = ev.length; i < len; i++){
					if(ev[i].startStr == date2){
						st += ev[i].title; 
						st += '\n';
					}
				}
				st += 'OKで勤怠編集画面'
				  if(confirm(st) && (st != '')) {
				    // OKの処理
					 window.location.href = "/admin/attendanceManage";
				  } else {
				    // キャンセルとか
				  }
								
			  },*/
			  eventClick: function(info){
				  var date2 = info.event.startStr;
				  var ev = info.view.calendar.getEvents();
					var st= '';
					for(var i = 0, len = ev.length; i < len; i++){
						if(ev[i].startStr == date2){
							st += ev[i].title; 
							st += '\n';
						}
					}
					st += 'OKで勤怠編集画面'
					  if(confirm(st) && (st != '')) {
					    // OKの処理
						  window.location.href = "/admin/attendanceManage";
					  } else {
					    // キャンセルとか
					  }
			  }
			
	});
        calendar.render();
      });