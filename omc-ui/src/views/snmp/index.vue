<template>
  <div class="app-container">
    <el-form ref="form" :model="form" label-width="120px">
      <el-form-item label="IP">
        <el-input v-model="form.ip"/>
      </el-form-item>
      <el-form-item label="Community">
        <el-input v-model="form.community"/>
      </el-form-item>
      <el-form-item label="Method">
        <el-select v-model="form.type" placeholder="method">
          <el-option label="get" value="get"/>
          <el-option label="set" value="set"/>
          <el-option label="getList" value="getList"/>
          <el-option label="getListAsync" value="getListAsync"/>
          <el-option label="getTree" value="getTree"/>
          <el-option label="getTreeAsync" value="getTreeAsync"/>
        </el-select>
      </el-form-item>
      <el-form-item label="Oid">
        <el-input v-model="form.oid"/>
      </el-form-item>
      <el-form-item label="Oids">
        <el-input v-model="form.oids" type="textarea"/>
      </el-form-item>
      <el-form-item label="Value">
        <el-input v-model="form.value"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">Create</el-button>
        <el-button @click="onCancel">Cancel</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

import { set, get, getList, getListAsync, getTree, getTreeAsync } from '@/api/snmp'
export default {
  data() {
    return {
      form: {
        ip: '127.0.0.1',
        community: 'public',
        oid: '1.3.6.1.2.1.1.5.0',
        oids: '',
        value: '',
        type: ''
      }
    }
  },
  methods: {
    onSubmit() {
      const { ip, community, oid, oids, value } = this.form
      switch(this.form.type) {
        case 'get':
          get({ ip: ip, community: community, oid: oid }).then(response => {
            console.log(JSON.stringify(response))
            this.$message(JSON.stringify(response.data))
          })
          break;
        case 'getList':
          getList({ ip: ip, community: community, oids: oids }).then(response => {
            this.$message(JSON.stringify(response.data))
          })
          break;
        case 'getListAsync':
          getListAsync({ ip: ip, community: community, oids: oids }).then(response => {
            this.$message(JSON.stringify(response.data))
          })
          break;
        case 'getTree':
          getTree({ ip: ip, community: community, oid: oid }).then(response => {
            this.$message(JSON.stringify(response.data))
          })
          break;
        case 'getTreeAsync':
          getTreeAsync({ ip: ip, community: community, oid: oid }).then(response => {
            this.$message(JSON.stringify(response.data))
          })
          break;
        case 'set':
          set({ ip: ip, community: community, oid: oid, value: value }).then(response => {
            this.$message(JSON.stringify(response.data))
          })
          break;
        default:
          break;  
      }
    },
    onCancel() {
      this.$message({
        message: 'cancel!',
        type: 'warning'
      })
    }
  }
}
</script>

<style scoped>
.line {
  text-align: center;
}
</style>

