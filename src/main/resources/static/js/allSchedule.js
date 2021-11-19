document.addEventListener('DOMContentLoaded', function() {
	  var calendarEl = document.getElementById('allSchedule');
        var calendar = new FullCalendar.Calendar(calendarEl, {
			locale:'ja',
			themeSystem: 'bootstrap',
        	contentHeight: 'auto',
        	showNonCurrentDates: false,
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
				  alert(st);
								
			  },
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
				
					alert(st);
			  }
			
	});
        calendar.render();
      });