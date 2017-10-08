(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('ParticipantDetailController', ParticipantDetailController);

    ParticipantDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Participant', 'Study'];

    function ParticipantDetailController($scope, $rootScope, $stateParams, previousState, entity, Participant, Study) {
        var vm = this;

        vm.participant = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('ohmageApp:participantUpdate', function(event, result) {
            vm.participant = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
