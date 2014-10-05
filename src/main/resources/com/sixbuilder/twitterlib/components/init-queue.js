(function ($) {
  $.extend(Tapestry.Initializer, {
    "initQueue": function (params) {
      React.renderComponent(QueueWidget({
        callbacks: params.callbacks,
        lazyLoad: params.lazyLoad,
        updateQueueViewURL: params.updateQueueViewURL}),
        document.getElementById(params.element));
    }
  })
})(jQuery);
