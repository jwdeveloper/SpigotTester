Vue.component('summary-component', {
    props: ['report'],
    mounted() {
        console.log("siema")
    },
    data: function () {
        return {
            alerts: [],
        }
    },
    methods: {
     
    },
    template: `
    <div >
         <h3>Report ID: <span>{{ report.reportId }}</span></h1>
         <h3>Created At: <span>{{ report.createdAt }}</span></h3>
         <h3>Server Version: <span>{{ report.serverVersion }}</span></h3>
         <h3>Spigot Version: <span>{{ report.spigotVersion }}</span></h3>
         <h3>Spigot Tester Version: <span>{{ report.spigotTesterVersion }}</span></h3>
         <br>


      
         <div class="row">

      

         <div v-for="plugin in report.plugins" class="col-6">
         <plugin-component v-bind:data="plugin"></plugin-component>
         </div>

         <div class="col-6">
         </div>
         </div>
       
    </div>
      `})


   