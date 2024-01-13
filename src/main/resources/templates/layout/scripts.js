// common-scripts-tags.js

// Function to dynamically create script tags and append them to the document head
function includeScript(src) {
    var script = document.createElement('script');
    script.src = src;
    document.head.appendChild(script);
}

// Include required vendors
includeScript('../cdn-cgi/scripts/5c5dd728/cloudflare-static/email-decode.min.js');
includeScript('vendor/global/global.min.js');
includeScript('vendor/bootstrap-select/dist/js/bootstrap-select.min.js');
includeScript('vendor/chart.js/Chart.bundle.min.js');
includeScript('vendor/peity/jquery.peity.min.js');
includeScript('vendor/apexchart/apexchart.js');
includeScript('vendor/owl-carousel/owl.carousel.js');

// Include dashboard scripts
includeScript('js/dashboard/dashboard-1.js');

// Include DataTables
includeScript('vendor/datatables/js/jquery.dataTables.min.js');
includeScript('js/plugins-init/datatables.init.js');

// Include SmartWizard
includeScript('vendor/jquery-smartwizard/dist/js/jquery.smartWizard.js');

// Include additional scripts
includeScript('js/custom.min.js');
includeScript('js/deznav-init.js');
includeScript('js/demo.js');
includeScript('js/styleSwitcher.js');
includeScript('https://cdn.jsdelivr.net/npm/sweetalert2@10');
