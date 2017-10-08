(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('SurveyDialogController', SurveyDialogController);

    SurveyDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Survey', 'Study'];

    function SurveyDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Survey, Study) {
        var vm = this;

        vm.survey = entity;
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
            if (vm.survey.id !== null) {
                Survey.update(vm.survey, onSaveSuccess, onSaveError);
            } else {
                Survey.save(vm.survey, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ohmageApp:surveyUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
