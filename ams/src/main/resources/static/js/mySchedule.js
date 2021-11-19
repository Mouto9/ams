document.addEventListener('DOMContentLoaded', function() {
	  var calendarEl = document.getElementById('mySchedule');
        var calendar = new FullCalendar.Calendar(calendarEl, {
			locale:'ja',
			dayCellContent: function(e) {
    		e.dayNumberText = e.dayNumberText.replace('日', '');
			},
			eventMouseEnter: function(mouseEnterInfo){
				mouseEnterInfo.event = eventSources;
				mouseEnterInfo.element = title;
				
			},
			eventSources:[
				{
					url:'/myScheduleURL',
					
				}
			]

        });
        calendar.render();
      });