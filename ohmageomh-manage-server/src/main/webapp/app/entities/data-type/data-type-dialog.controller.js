(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DataTypeDialogController', DataTypeDialogController);

    DataTypeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'DataType', 'Integration'];

    function DataTypeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, DataType, Integration) {
        var vm = this;

        vm.dataType = entity;
        vm.clear = clear;
        vm.save = save;
        vm.integrations = Integration.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.dataType.id !== null) {
                DataType.update(vm.dataType, onSaveSuccess, onSaveError);
            } else {
                DataType.save(vm.dataType, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('ohmageApp:dataTypeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
