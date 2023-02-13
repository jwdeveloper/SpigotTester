Vue.component('test-method-component', {
    props: ['data'],
    mounted() {
      
    },
    data: function () {
        return {
            plugin:{}
        }
    },
    methods: {
     
    },
    template: `
     <div class ="tests-panle-method" v-bind:class="{ 'passed-method': data.isPassed }">
     
     <div class="tests-panel-info row">
          <div class="tests-panel-info-time col-2>
          <i v-if="data.isPassed == false" class="fas fa-exclamation-circle not-passed"></i>
          <i v-else class="fas fa-check-circle fa-xl passed"></i>
          {{data.name}}
           </div>
        
        <div class="tests-panel-info-total col-2 row">
           <i class="fas fa-stopwatch col"></i>
           <p class="col">{{data.executionTime}}ms</p>
        </div>
     </div>

     <div v-if="data.isPassed == false" class="stack-trace-box" >
       A simple secondary alert—check it out!
      </div>

   </div>

     </div>
      `})