(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('StudyDetailController', StudyDetailController);

    StudyDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Study', 'User', 'Survey', 'Integration', 'Participant', 'Organization'];

    function StudyDetailController($scope, $rootScope, $stateParams, previousState, entity, Study, User, Survey, Integration, Participant, Organization) {
        var vm = this;

        vm.study = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('ohmageApp:studyUpdate', function(event, result) {
            vm.study = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
