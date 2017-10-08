(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('IntegrationDetailController', IntegrationDetailController);

    IntegrationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Integration', 'DataType', 'Study'];

    function IntegrationDetailController($scope, $rootScope, $stateParams, previousState, entity, Integration, DataType, Study) {
        var vm = this;

        vm.integration = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('ohmageApp:integrationUpdate', function(event, result) {
            vm.integration = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
