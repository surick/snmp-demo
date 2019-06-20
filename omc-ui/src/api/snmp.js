import request from '@/utils/request'

export function set(data) {
	return request({
		url: '/snmp/set',
		method: 'post',
		data
	})
}

export function get(data) {
	return request({
		url: '/snmp/get',
		method: 'post',
		data
	})
}

export function getList(data) {
	return request({
		url: '/snmp/getList',
		method: 'post',
		data
	})
}

export function getListAsync(data) {
	return request({
		url: '/snmp/getListAsync',
		method: 'post',
		data
	})
}

export function getTree(data) {
	return request({
		url: '/snmp/getTree',
		method: 'post',
		data
	})
}

export function getTreeAsync(data) {
	return request({
		url: '/snmp/getTreeAsync',
		method: 'post',
		data
	})
}
