Vue.component('test-class-component', {
    props: ['data'],
    mounted() {
        console.log(this.data)
        for(var method of this.data.testMethods)
        {
            this.totalTests +=1;
            this.totalTime += method.executionTime;
            this.totalPassed += method.isPassed == true?1:0
        }
        this.totalTime =   this.totalTime.toFixed(4);
    },
    methods: {
        changeVisibility()
        {
            this.hide = !this.hide;
        }
    },
    data: function () {
        return {
            totalTime:0,
            totalTests:0,
            totalPassed:0,
            hide:false
        }
    },
    template: `
     <div class="tests-panel" >
         
      <div class="tests-class-panel" v-on:click="changeVisibility()">
        <h4 class="font-weight-normal">{{data.className}}</h4>
        <div class="tests-panel-info">
        <div class="tests-panel-info-passed passed">
           {{totalPassed}}
           <i class="fas fa-check"></i>
          </div>

           <div class="tests-panel-info-total">
              <i class="fas fa-file"></i>
              {{totalTests}}
           </div>

           <div class="tests-panel-info-time ">
           <i class="fas fa-stopwatch"></i>
           {{totalTime}}ms
         </div>
        </div>
      </div>

       <div v-if="hide == false" v-for="method in data.testMethods">
        <test-method-component  v-bind:data="method"> </test-method-component>
      </div>
      
     </div>
      `})