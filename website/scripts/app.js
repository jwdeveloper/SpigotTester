new Vue(
    {
        el: '#app',
        data: {
            global:
            {
                report: {}
            },
        },
        mounted() {
            fetch('resources/report.json')
                .then(response => response.json())
                .then(data => {
                    this.global.report = data;
                    console.log(data)
                });
        }
    })