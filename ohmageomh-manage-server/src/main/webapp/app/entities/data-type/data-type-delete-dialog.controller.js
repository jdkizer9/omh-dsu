(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DataTypeDeleteController',DataTypeDeleteController);

    DataTypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'DataType'];

    function DataTypeDeleteController($uibModalInstance, entity, DataType) {
        var vm = this;

        vm.dataType = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            DataType.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
