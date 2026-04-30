import request from '@/utils/request'

export function getClients(params) {
  return request({
    url: '/clients',
    method: 'get',
    params
  })
}

export function getAllClients() {
  return request({
    url: '/clients/all',
    method: 'get'
  })
}

export function getClient(id) {
  return request({
    url: `/clients/${id}`,
    method: 'get'
  })
}

export function createClient(data) {
  return request({
    url: '/clients',
    method: 'post',
    data
  })
}

export function updateClient(id, data) {
  return request({
    url: `/clients/${id}`,
    method: 'put',
    data
  })
}

export function deleteClient(id) {
  return request({
    url: `/clients/${id}`,
    method: 'delete'
  })
}

export function getCases(params) {
  return request({
    url: '/cases',
    method: 'get',
    params
  })
}

export function getCase(id) {
  return request({
    url: `/cases/${id}`,
    method: 'get'
  })
}

export function createCase(data) {
  return request({
    url: '/cases',
    method: 'post',
    data
  })
}

export function updateCase(id, data) {
  return request({
    url: `/cases/${id}`,
    method: 'put',
    data
  })
}

export function deleteCase(id) {
  return request({
    url: `/cases/${id}`,
    method: 'delete'
  })
}

export function updateCaseStatus(id, status) {
  return request({
    url: `/cases/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function getCaseStatsByStatus() {
  return request({
    url: '/cases/stats/by-status',
    method: 'get'
  })
}

export function getCaseStatsByType() {
  return request({
    url: '/cases/stats/by-type',
    method: 'get'
  })
}

export function getCaseStatsByLawyer() {
  return request({
    url: '/cases/stats/by-lawyer',
    method: 'get'
  })
}

export function getCaseTypes() {
  return request({
    url: '/cases/types',
    method: 'get'
  })
}

export function getAllCases() {
  return request({
    url: '/cases/all',
    method: 'get'
  })
}

export function getContracts(params) {
  return request({
    url: '/contracts',
    method: 'get',
    params
  })
}

export function getContract(id) {
  return request({
    url: `/contracts/${id}`,
    method: 'get'
  })
}

export function createContract(data) {
  return request({
    url: '/contracts',
    method: 'post',
    data
  })
}

export function updateContract(id, data) {
  return request({
    url: `/contracts/${id}`,
    method: 'put',
    data
  })
}

export function deleteContract(id) {
  return request({
    url: `/contracts/${id}`,
    method: 'delete'
  })
}

export function signContract(id) {
  return request({
    url: `/contracts/${id}/sign`,
    method: 'put'
  })
}

export function getContractStats() {
  return request({
    url: '/contracts/stats',
    method: 'get'
  })
}

export function getFees(params) {
  return request({
    url: '/fees',
    method: 'get',
    params
  })
}

export function getFee(id) {
  return request({
    url: `/fees/${id}`,
    method: 'get'
  })
}

export function createFee(data) {
  return request({
    url: '/fees',
    method: 'post',
    data
  })
}

export function updateFee(id, data) {
  return request({
    url: `/fees/${id}`,
    method: 'put',
    data
  })
}

export function deleteFee(id) {
  return request({
    url: `/fees/${id}`,
    method: 'delete'
  })
}

export function getFeeSummary() {
  return request({
    url: '/fees/stats/summary',
    method: 'get'
  })
}

export function getMonthlyIncome(year) {
  return request({
    url: '/fees/stats/monthly',
    method: 'get',
    params: { year }
  })
}
