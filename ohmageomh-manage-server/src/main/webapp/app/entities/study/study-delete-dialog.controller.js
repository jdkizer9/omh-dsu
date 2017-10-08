(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('StudyDeleteController',StudyDeleteController);

    StudyDeleteController.$inject = ['$uibModalInstance', 'entity', 'Study'];

    function StudyDeleteController($uibModalInstance, entity, Study) {
        var vm = this;

        vm.study = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Study.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
