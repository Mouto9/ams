document.addEventListener('DOMContentLoaded', function() {
		
	  var calendarEl = document.getElementById('test');
        var calendar = new FullCalendar.Calendar(calendarEl, {
			locale:'ja',
			editable:"true",
			businessHours: true,
			dayCellContent: function(e) {
    			e.dayNumberText = e.dayNumberText.replace('日', '');
			},
		
			events:[
				{
					url: '/testCalendar',
				},{
					url:  'https://holidays-jp.github.io/api/v1/date.json'
				}
			],
			/*eventClick: function(info) {
 				alert(info.event.title);
				
			  },*/
			dateClick: function(info) {
					
    			
    			info.dayEl.style.backgroundColor = '#aba8a4';
 			 }

        });
        calendar.render();
      });
