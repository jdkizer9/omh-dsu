(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('StudyDialogController', StudyDialogController);

    StudyDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Study', 'User', 'Survey', 'Integration', 'Participant', 'Organization'];

    function StudyDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Study, User, Survey, Integration, Participant, Organization) {
        var vm = this;

        vm.study = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();
        vm.surveys = Survey.query();
        vm.integrations = Integration.query();
        vm.participants = Participant.query();
        vm.organizations = Organization.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.study.id !== null) {
                Study.update(vm.study, onSaveSuccess, onSaveError);
            } else {
                Study.save(vm.study, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ohmageApp:studyUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.startDate = false;
        vm.datePickerOpenStatus.endDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
