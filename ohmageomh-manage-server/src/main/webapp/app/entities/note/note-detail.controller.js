(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('NoteDetailController', NoteDetailController);

    NoteDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Note', 'Study', 'User', 'Participant'];

    function NoteDetailController($scope, $rootScope, $stateParams, previousState, entity, Note, Study, User, Participant) {
        var vm = this;

        vm.note = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('ohmageApp:noteUpdate', function(event, result) {
            vm.note = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
