(function ($) {
  $.extend(Tapestry.Initializer, {
    "initQueue": function (params) {
      React.renderComponent(QueueWidget({
        callbacks: params.callbacks,
        lazyLoad: params.lazyLoad}),
        document.getElementById(params.element));
    }
  })
})(jQuery);
