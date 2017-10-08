(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('IntegrationDialogController', IntegrationDialogController);

    IntegrationDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Integration', 'DataType', 'Study'];

    function IntegrationDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Integration, DataType, Study) {
        var vm = this;

        vm.integration = entity;
        vm.clear = clear;
        vm.save = save;
        vm.datatypes = DataType.query();
        vm.studies = Study.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.integration.id !== null) {
                Integration.update(vm.integration, onSaveSuccess, onSaveError);
            } else {
                Integration.save(vm.integration, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ohmageApp:integrationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
