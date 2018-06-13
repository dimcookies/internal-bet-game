export default function SafeHTMLFilter($sce){
    return function(val) {
        return $sce.trustAsHtml(val);
    };
}

SafeHTMLFilter.$inject = ['$sce'];