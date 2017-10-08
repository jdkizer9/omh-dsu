(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('OrganizationDetailController', OrganizationDetailController);

    OrganizationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Organization', 'Study', 'User'];

    function OrganizationDetailController($scope, $rootScope, $stateParams, previousState, entity, Organization, Study, User) {
        var vm = this;

        vm.organization = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('ohmageApp:organizationUpdate', function(event, result) {
            vm.organization = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
