<template>
  <div class="author-card" :style="{ animationDelay: delay }">
    <h3 class="author-name">{{ name }}</h3>
    <div class="separator"></div>
    <p class="author-role">{{ role }}</p>
  </div>
</template>

<script setup>
defineProps({
  name: String,
  role: String,
  delay: String
});
</script>

<style scoped>
.author-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  background-color: white;
  border-radius: 0.75rem;
  border: 1px solid #e7e5e4;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  width: 100%;
  max-width: 20rem;
  animation: fadeInUp 0.6s ease-out forwards;
  opacity: 0;
}

.author-card:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  border-color: rgba(197, 160, 89, 0.5);
}

.author-name {
  font-family: serif;
  font-size: 1.5rem;
  color: #1c1917;
  text-align: center;
  margin-bottom: 0.75rem;
}

.separator {
  width: 3rem;
  height: 0.125rem;
  background-color: #C5A059;
  margin-bottom: 1rem;
  opacity: 0.6;
}

.author-role {
  font-size: 0.75rem;
  color: #78716c;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  text-align: center;
  line-height: 1.625;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
