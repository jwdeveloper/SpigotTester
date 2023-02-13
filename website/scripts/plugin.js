Vue.component('plugin-component', {
    props: ['data'],
    mounted() {
      
    },
    data: function () {
        return {
            hide:false
        }
    },
    methods: {
        changeVisibility()
        {
            this.hide = !this.hide;
        }
    },
    template: `

    <div class="tests-panel-plugin">
    <div class="tests-class-panel" v-on:click="changeVisibility()">
      <h4 class="font-weight-normal">{{data.pluginName}}</h4>
      <h4 class="font-weight-normal">version: {{data.pluginVersion}}</h4>
      <div class="tests-panel-info">
      <div class="tests-panel-info-passed passed">
        
         <i class="fas fa-check"></i>
        </div>

         <div class="tests-panel-info-total">
            <i class="fas fa-file"></i>
           
         </div>

         <div class="tests-panel-info-time ">
         <i class="fas fa-stopwatch"></i>
       ms
       </div>
      </div>
    </div>


    

     <div v-if="hide == false" v-for="classTest in data.classResults" >
     <test-class-component  v-bind:data="classTest"> </test-class-component>
    </div>
   </div>
      `})