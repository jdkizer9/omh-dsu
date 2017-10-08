(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('data-type', {
            parent: 'entity',
            url: '/data-type?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'DataTypes'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/data-type/data-types.html',
                    controller: 'DataTypeController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('data-type-detail', {
            parent: 'data-type',
            url: '/data-type/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'DataType'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/data-type/data-type-detail.html',
                    controller: 'DataTypeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'DataType', function($stateParams, DataType) {
                    return DataType.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'data-type',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('data-type-detail.edit', {
            parent: 'data-type-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/data-type/data-type-dialog.html',
                    controller: 'DataTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DataType', function(DataType) {
                            return DataType.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('data-type.new', {
            parent: 'data-type',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/data-type/data-type-dialog.html',
                    controller: 'DataTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                schemaNamespace: null,
                                schemaName: null,
                                schemaVersion: null,
                                csvMapper: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('data-type', null, { reload: 'data-type' });
                }, function() {
                    $state.go('data-type');
                });
            }]
        })
        .state('data-type.edit', {
            parent: 'data-type',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/data-type/data-type-dialog.html',
                    controller: 'DataTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DataType', function(DataType) {
                            return DataType.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('data-type', null, { reload: 'data-type' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('data-type.delete', {
            parent: 'data-type',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/data-type/data-type-delete-dialog.html',
                    controller: 'DataTypeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['DataType', function(DataType) {
                            return DataType.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('data-type', null, { reload: 'data-type' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
