<template>
  <!-- This is a reusable modal (pop-up box) template that can be used by simply importing it.
       You can override the default content by doing something like '<div slot="body"></div>'.
       Note: emit events don't seem to work in slot content despite the main devs acknowledging this problem years ago :/.
             Workaround is to add a method in your main component that does the same thing.
  -->
  <div>
    <div class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">
          <div class="modal-header">
            <slot name="header">
              Default Header
            </slot>
          </div>

          <div class="modal-body">
            <slot name="body">
              Default Body
            </slot>
          </div>

          <div class="modal-footer">
            <slot name="footer">
              <button class="modal-ok-button" @click="close">
                Close
              </button>
            </slot>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Modal",
  methods: {
    close() {
      this.$emit('close');
    },
  },
};
</script>

<style scoped>

.modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: table;
  transition: opacity 0.3s ease;
}

.modal-wrapper {
  display: table-cell;
  vertical-align: middle;
}

.modal-container {
  width: 350px;
  margin: 0 auto;
  padding: 1em 2em;
  border-radius: 4px;
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
  background-color: #FFFFFF;
}

.modal-header {
  font-size: 18px;
  text-align: center;
  padding: 1em 0;
}

.modal-body {
  text-align: center;
  padding: 1em 0;
}

.modal-footer {
  padding: 1em 0;
  text-align: center;
}





</style>