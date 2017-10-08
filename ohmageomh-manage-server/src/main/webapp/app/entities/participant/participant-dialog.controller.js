(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('ParticipantDialogController', ParticipantDialogController);

    ParticipantDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Participant', 'Study'];

    function ParticipantDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Participant, Study) {
        var vm = this;

        vm.participant = entity;
        vm.clear = clear;
        vm.save = save;
        vm.studies = Study.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.participant.id !== null) {
                Participant.update(vm.participant, onSaveSuccess, onSaveError);
            } else {
                Participant.save(vm.participant, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ohmageApp:participantUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
