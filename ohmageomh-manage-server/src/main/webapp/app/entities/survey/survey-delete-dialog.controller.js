(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('SurveyDeleteController',SurveyDeleteController);

    SurveyDeleteController.$inject = ['$uibModalInstance', 'entity', 'Survey'];

    function SurveyDeleteController($uibModalInstance, entity, Survey) {
        var vm = this;

        vm.survey = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Survey.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
