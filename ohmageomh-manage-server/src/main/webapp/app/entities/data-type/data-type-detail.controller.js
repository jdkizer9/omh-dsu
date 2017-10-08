(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DataTypeDetailController', DataTypeDetailController);

    DataTypeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'DataType', 'Integration'];

    function DataTypeDetailController($scope, $rootScope, $stateParams, previousState, entity, DataType, Integration) {
        var vm = this;

        vm.dataType = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('ohmageApp:dataTypeUpdate', function(event, result) {
            vm.dataType = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
