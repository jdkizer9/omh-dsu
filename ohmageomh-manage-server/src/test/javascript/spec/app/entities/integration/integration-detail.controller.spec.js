'use strict';

describe('Controller Tests', function() {

    describe('Integration Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockIntegration, MockDataType, MockStudy;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockIntegration = jasmine.createSpy('MockIntegration');
            MockDataType = jasmine.createSpy('MockDataType');
            MockStudy = jasmine.createSpy('MockStudy');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Integration': MockIntegration,
                'DataType': MockDataType,
                'Study': MockStudy
            };
            createController = function() {
                $injector.get('$controller')("IntegrationDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'ohmageApp:integrationUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
