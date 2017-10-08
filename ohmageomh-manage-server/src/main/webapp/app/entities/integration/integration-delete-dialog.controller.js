(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('IntegrationDeleteController',IntegrationDeleteController);

    IntegrationDeleteController.$inject = ['$uibModalInstance', 'entity', 'Integration'];

    function IntegrationDeleteController($uibModalInstance, entity, Integration) {
        var vm = this;

        vm.integration = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Integration.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
