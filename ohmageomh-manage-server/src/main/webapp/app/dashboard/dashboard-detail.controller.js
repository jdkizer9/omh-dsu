(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DashboardDetailController', DashboardDetailController);

    DashboardDetailController.$inject = ['$scope', '$state', '$stateParams', '_', 'Study', 'Data', 'AlertService']

    function DashboardDetailController($scope, $state, $stateParams, _, Study, Data, AlertService) {
        // Make sure a study is specified
        if(typeof $stateParams.study == 'undefined' || typeof $stateParams.participant == 'undefined'){
            $state.go('home');
        }

        var vm = this;

        vm.handleDataResponse = handleDataResponse;
        vm.createEvent = createEvent;

        vm.study = Study.get({id: $stateParams.study},
            function(study){
                _.each(study.dataTypes, function(dataType, index) {
                    Data.query({
                        participant: $stateParams.participant,
                        schema_namespace: dataType.schemaNamespace,
                        schema_name: dataType.schemaName,
                        schema_version: dataType.schemaVersion,
                        limit: 5000
                    }, function(data){vm.handleDataResponse(data, dataType);});
                });
            });
        vm.participant = Study.getParticipant({id: $stateParams.study, participant: $stateParams.participant});

        function handleDataResponse(data, dataType) {
            if(data.length == 0){
                dataType.summary = "0 data points";
            }
            else {
                dataType.summary = data.length + " data points";
                _.each(data, function(dataPoint) {
                    var date = new Date(eval('dataPoint.' + dataType.dateField));
                    vm.events.push(vm.createEvent(dataType.name, date, dataType.color));
                    if(dataType.lastDataPointDate == null || dataType.lastDataPointDate < date) {
                        dataType.lastDataPointDate = date;
                    }
                });
            }
        }

        function createEvent(title, date, type) {
            return {
                title: title,
                type: type,
                startsAt: date,
                draggable: false,
                resizable: false,
                editable: false,
                deletable: false,
                allDay: false
            }
        }

        //These variables MUST be set as a minimum for the calendar to work
        vm.calendarView = 'month';
        vm.viewDate = new Date();
        vm.isCellOpen = true;
        vm.events = [];

        vm.modifyCell = function(cell) {
            console.log(cell);
        };

        vm.toggle = function($event, field, event) {
            $event.preventDefault();
            $event.stopPropagation();
            event[field] = !event[field];
        };
    }
})();
